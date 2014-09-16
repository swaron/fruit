package me.suisui.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import lombok.Data;
import me.suisui.data.jdbc.po.pub.FileUpload;
import me.suisui.framework.paging.PagingParam;
import me.suisui.integration.spring.cache.GuavaCacheManager;
import me.suisui.repo.jdbc.dao.pub.UploadDao;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;
import net.coobird.thumbnailator.geometry.Positions;

import org.apache.catalina.Globals;
import org.apache.catalina.core.ApplicationPart;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.cache.Cache;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

@WebServlet(name = "UploadServlet", urlPatterns = { "/upload.do" }, initParams = @WebInitParam(name = "cacheSeconds", value = "31536000"))
@MultipartConfig(maxFileSize = 100 * 1024 * 1024)
public class UploadServlet extends HttpServlet {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	private static final long serialVersionUID = 1L;

	File uploadDir;
	private UploadDao uploadDao;
	GuavaCacheManager cacheManager;
	Cache thumbCache;
	private String urlPrefix;
	private ObjectMapper objectMapper = new ObjectMapper();
	private int cacheSeconds = 0;
	
	
	@Override
	public void init() throws ServletException {
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		uploadDao = wac.getBean(UploadDao.class);
		cacheManager = wac.getBean(GuavaCacheManager.class);
		urlPrefix = getServletContext().getContextPath() + "/upload.do";
		String location = getServletContext().getInitParameter("app.upload.location");
		cacheSeconds = NumberUtils.toInt(getInitParameter("cacheSeconds"), 0);
		if (location == null) {
			logger.warn("upload location not specified, using default location.");
			File tmp = (File) getServletContext().getAttribute(ServletContext.TEMPDIR) ;
			uploadDir = new File(tmp.getParentFile(),"upload");
		}else{
			FileSystemResourceLoader resourceLoader = new FileSystemResourceLoader();
			Resource resource = resourceLoader.getResource(location);
			try {
				uploadDir = resource.getFile();
				if (uploadDir.canRead() && uploadDir.isDirectory()) {
				} else {
					boolean mkdirs = resource.getFile().mkdirs();
					if (!mkdirs) {
						logger.error("unable to create directory specified in env. the directory value is: {}",
								resource.getDescription());
						throw new ServletException("failed to init upload directory.");
					}
				}
			} catch (IOException e) {
				logger.error("unable to resolve upload directory. {}", resource.getDescription());
				throw new ServletException("unable to resolve upload directory.");
			}
		}

		if (!uploadDir.canRead()) {
			logger.error("upload directory unreadble. directory: {}", uploadDir.getAbsolutePath());
			throw new ServletException("upload directory unreadble. directory: {}");
		}
		logger.debug("initialized upload directory to : {}", uploadDir.getAbsolutePath());
		
		thumbCache = cacheManager.getCache("UploadServlet.thumbCache");
	}

	protected final void checkAndPrepare(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		applyCacheSeconds(response, cacheSeconds);
	}

	protected final void applyCacheSeconds(HttpServletResponse response, int seconds) {
		if (seconds > 0) {
			cacheForSeconds(response, seconds);
		} else if (seconds == 0) {
			preventCaching(response);
		}
		// Leave caching to the client otherwise.
	}

	protected final void cacheForSeconds(HttpServletResponse response, int seconds) {
		String headerValue = "max-age=" + seconds;
		headerValue += ", must-revalidate";
		response.setHeader("Cache-Control", headerValue);
	}

	protected final void preventCaching(HttpServletResponse response) {
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// uploadResult.setUrl("./upload.do?id=" + filename);
		// uploadResult.setThumbnailUrl("./upload.do?id=" + filename +
		// "&w=300&h=300");
		String id = req.getParameter("id");
		if (id == null) {
			logger.debug("Missing Id parameter - returning 400");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		checkAndPrepare(req, resp);
		int width = NumberUtils.toInt(req.getParameter("w"), 0);
		int height = NumberUtils.toInt(req.getParameter("h"), 0);
		String md5 = StringUtils.substring(id, 0, 128 / 4);
		String lengthStr = StringUtils.substring(id, 128 / 4);
//		long length = Long.parseLong(lengthStr, 16);
		File file = FileUtils.getFile(uploadDir, id.substring(0, 1), id);
		if (!file.canRead()) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND, "文件不存在");
			return;
		}

		if (width < 10000 && height < 5000 && (width > 0 || height > 0)) {
			handleThumb(req, resp, file, width, height);
		} else {
			boolean sendfile = checkSendfile(req, resp, file, null);
			if (!sendfile) {
				FileUtils.copyFile(file, resp.getOutputStream());
			}
		}

	}

	protected boolean checkSendfile(HttpServletRequest request, HttpServletResponse response, File entry, Range range) {
		if ((Boolean.TRUE == request.getAttribute(Globals.SENDFILE_SUPPORTED_ATTR))
				&& (request.getClass().getName().equals("org.apache.catalina.connector.RequestFacade"))
				&& (response.getClass().getName().equals("org.apache.catalina.connector.ResponseFacade"))
				&& (entry != null) && (entry.canRead())) {
			try {
				request.setAttribute(Globals.SENDFILE_FILENAME_ATTR, entry.getCanonicalPath());
				if (range == null) {
					request.setAttribute(Globals.SENDFILE_FILE_START_ATTR, Long.valueOf(0L));
					request.setAttribute(Globals.SENDFILE_FILE_END_ATTR, Long.valueOf(entry.length()));
					response.setContentLength((int) entry.length());

					// FileInputStream fileInputStream = new
					// FileInputStream(entry);
					// FileChannel fileChannel = fileInputStream.getChannel();
					// WritableByteChannel target ;
					// fileChannel.transferTo(0L, entry.length());
				} else {
					request.setAttribute(Globals.SENDFILE_FILE_START_ATTR, Long.valueOf(range.getMin()));
					request.setAttribute(Globals.SENDFILE_FILE_END_ATTR, Long.valueOf(range.getMax() + 1));
					response.setContentLength(range.getMax() - range.getMin() + 1);
				}

				return true;
			} catch (IOException e) {
				return false;
			}
		}
		return false;
	}

	private void handleThumb(HttpServletRequest req, HttpServletResponse resp, File source, int width, int height)
			throws IOException {
		
		boolean storeDisk = (width % 60 == 0) && (width == height || height == 0);  
		String thumbFileName = source.getCanonicalPath() + "." + width + "x" + height + ".JPEG";
		if(storeDisk){
			File thumbFile = new File(thumbFileName);
			boolean sendfile = checkSendfile(req, resp, thumbFile, null);
			if (!sendfile) {
				if (!thumbFile.canRead()) {
					Builder<File> builder = Thumbnails.of(source);
					if(width != 0){
						builder.width(width);
					}
					if(height != 0){
						builder.height(height);
					}
					if(height != 0 && width != 0){
						builder.crop(Positions.CENTER);
					}
					builder.toFile(thumbFile);
				}
				FileUtils.copyFile(thumbFile, resp.getOutputStream());
			}
		}else{
			byte[] thumb = thumbCache.get(thumbFileName, byte[].class);
			if(thumb == null){
				Builder<File> builder = Thumbnails.of(source);
				if(width != 0){
					builder.width(width);
				}
				if(height != 0){
					builder.height(height);
				}
				if(height != 0 && width != 0){
					builder.crop(Positions.CENTER);
				}
				builder.outputFormat("JPEG");
				ByteArrayOutputStream os = new ByteArrayOutputStream(4000);
				builder.toOutputStream(os);
				byte[] array = os.toByteArray();
				thumb = array;
				thumbCache.put(thumbFileName, thumb);
			}
			resp.setContentLength(thumb.length);
			// Flush byte array to servlet output stream.
			ServletOutputStream out = resp.getOutputStream();
			out.write(thumb);
			out.flush();
		}
		
		
		
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String id = req.getParameter("id");
		String token = req.getParameter("token");
		logger.debug("handle file delete for id: {}, token: {}", id, token);
		if (id == null) {
			throw new MissingServletRequestParameterException("id", "String");
		}
		String md5 = StringUtils.substring(id, 0, 128 / 4);
		byte[] dbMd5 = org.springframework.security.crypto.codec.Hex.decode(md5);
		String lengthStr = StringUtils.substring(id, 128 / 4);
		long size = Long.parseLong(lengthStr, 16);
		List<FileUpload> list = findFile(dbMd5, size);
		if (list.isEmpty()) {
			logger.debug("unable to find file for delete, db5: {},size:{}", md5, size);
			resp.sendError(HttpServletResponse.SC_NOT_FOUND, "文件不存在");
			return;
		}
		FileUpload fileUpload = list.iterator().next();
		if (StringUtils.equals(token, fileUpload.getAuthToken())) {
			logger.debug("found db record for file {}", id);
			if (fileUpload.getFinished() != null && fileUpload.getFinished() == true) {
				logger.debug("当前文件{}正在上传中，无法删除.", id);

				File file = FileUtils.getFile(uploadDir, md5.substring(0, 1), id);
				if (!file.canRead()) {
					logger.debug("没有在文件系统里面找到文件{}.可以当做已经删除", id);
					resp.sendError(HttpServletResponse.SC_NOT_FOUND, "文件不存在，可能文件已经删除。");
					return;
				}
				boolean delete = file.delete();
				if (delete) {
					// 移除数据库记录
					logger.debug("文件已经从本地成功, file:{}.", id);
					uploadDao.remove(FileUpload.class, fileUpload.getUploadId());
					logger.debug("文件记录从数据库删除成功, file:{}.", id);
					HashMap<String, Object> result = new HashMap<String, Object>(1);
					List<Map<String, Boolean>> files = Lists.newArrayList();
					Map<String, Boolean> file1 = new HashMap<String, Boolean>();
					file1.put(fileUpload.getUploadFilename(), true);
					files.add(file1);
					result.put("files", files);
					objectMapper.writeValue(resp.getOutputStream(), result);
					return;
				} else {
					logger.debug("删除文件失败，可能没有权限, file:{}.", id);
					resp.sendError(HttpServletResponse.SC_FORBIDDEN, "删除文件失败，可能没有权限。");
					return;
				}
			}
		} else {
			logger.debug("found file for delete,but the token {} is not the same as in db {}", token,
					fileUpload.getAuthToken());
			resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Token错误，无法删除文件");
			return;
		}
	}

	// {"files": [
	// {
	// "picture1.jpg": true
	// },
	// {
	// "picture2.jpg": true
	// }
	// ]}

	private List<FileUpload> findFile(byte[] dbMd5, long size) {
		PagingParam pagingParam = new PagingParam();
		pagingParam.addFilter("=", "md5", dbMd5);
		pagingParam.addFilter("=", "size", size);
		// pagingParam.addFilter("=", "auth_token", token);
		List<FileUpload> list = uploadDao.findList(FileUpload.class, pagingParam);
		return list;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// 绑死了tomcat服务器哦
		ApplicationPart part = (ApplicationPart) req.getPart("files[]");
		String contentType = part.getContentType();
		String submittedFileName = part.getSubmittedFileName();
		Long fileSize = part.getSize();

		logger.debug("handle file upload, name:{},size:{}.", submittedFileName, fileSize);
		InputStream source = part.getInputStream();
		byte[] md5 = DigestUtils.md5(source);
		// 128位的md5，如果用varchar存在数据库里面效率不然uuid高，UUID也是128位，所以用UUID来存了。
		// UUID uuid = UUID.nameUUIDFromBytes(md5);
		String hexString = Hex.encodeHexString(md5);
		String filename = (hexString + Long.toHexString(fileSize)).toLowerCase();
		File subdir = new File(uploadDir, Character.toString(hexString.charAt(0)));
		if (!subdir.isDirectory()) {
			boolean mkdirs = subdir.mkdirs();
			logger.debug("creating folder for uploading. result: {}", mkdirs);
		}
		logger.debug("uploading file to folder: {}", subdir.getAbsolutePath());
		List<FileUpload> existFiles = findFile(md5, fileSize);
		FileUpload entity;
		if (existFiles.isEmpty()) {
			entity = new FileUpload();
			entity.setAuthToken(RandomStringUtils.randomAlphanumeric(32));
			entity.setFinished(false);
			entity.setMd5(md5);
			entity.setSize(fileSize);
			entity.setUploadFilename(submittedFileName);
			entity.setUploadTime(new Timestamp(System.currentTimeMillis()));
			uploadDao.persist(FileUpload.class, entity);
			logger.debug("create upload record in db for file: {}", filename);
			part.write(FileUtils.getFile(subdir, filename).getAbsolutePath());
			entity.setFinished(true);
			uploadDao.update(entity);
			logger.debug("uploading finished for file: {}", filename);
		} else {
			entity = existFiles.iterator().next();
			File file = FileUtils.getFile(subdir, filename);
			if (!file.exists()) {
				// 文件不存在，重新写一边
				part.write(file.getAbsolutePath());
			}
			logger.debug("file already exist in database, file: {}, return database record.", filename);
		}

		List<UploadResult> files = Lists.newArrayList();
		UploadResult uploadResult = UploadResult.fromEntity(entity, urlPrefix);
		files.add(uploadResult);

		Map<String, Object> result = new HashMap<String, Object>(1);
		result.put("files", files);
		ByteArrayOutputStream stream = new ByteArrayOutputStream(2048);
		objectMapper.writeValue(stream, result);
		res.setContentType("application/json");
		res.setContentLength(stream.size());
		// Flush byte array to servlet output stream.
		ServletOutputStream out = res.getOutputStream();
		stream.writeTo(out);
		out.flush();
	}

	public static String getFileType(Part p) {
		return FilenameUtils.getExtension(getFileName(p));
	}

	public static String getFileName(Part p) {
		String name = p.getHeader("content-disposition");
		String filename = StringUtils.substringBetween(name, "filename=\"", "\"");
		return filename;
	}

	@Data
	public static class UploadResult {
		public static UploadResult fromEntity(FileUpload entity, String url) {
			UploadResult uploadResult = new UploadResult();
			uploadResult.setName(entity.getUploadFilename());
			uploadResult.setSize(entity.getSize());
			byte[] md5 = entity.getMd5();
			String filename = (Hex.encodeHexString(md5) + Long.toHexString(entity.getSize())).toLowerCase();
			uploadResult.setUrl(url + "?id=" + filename);
			ArrayList<String> list = Lists.newArrayList("jpg", "jpeg", "png", "gif");
			if (list.contains(FilenameUtils.getExtension(entity.getUploadFilename()).toLowerCase())) {
				uploadResult.setThumbnailUrl(url + "?id=" + filename + "&w=300&h=300");
			}
			// token = hash(id+createtime)
			uploadResult.setDeleteUrl(url + "?id=" + filename + "&token=" + entity.getAuthToken());
			return uploadResult;
		}

		private String name;
		private Long size;
		private String url;
		private String thumbnailUrl;
		private String deleteUrl;
		private String deleteType = "DELETE";
		private String error;
	}
}
