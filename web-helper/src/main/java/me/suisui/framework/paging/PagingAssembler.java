package me.suisui.framework.paging;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Component
public class PagingAssembler {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    ObjectMapper objectMapper;

    public PagingAssembler() {
    	objectMapper = new ObjectMapper();
    	objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    	objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
	}
    
    public PagingParam toPagingParam(PagingForm form) {
        PagingParam param = new PagingParam();
        param.setStart(form.getStart());
        param.setLimit(form.getLimit());
        param.getSort().addAll(parseSorters(form.getSort()));
        param.getFilter().addAll(parseFilters(form.getFilter()));

        return param;
    }

    /**
     * turn filter parameters or sort parameters into a map
     * 
     * @param json
     * @param keyProp
     * @param valueProp
     * @return
     */
    private List<Filter> parseFilters(String json) {
        if (json == null) {
            return Collections.emptyList();
        }
        try {
            TypeReference<List<Filter>> ref = new TypeReference<List<Filter>>() {
            };

            return objectMapper.<List<Filter>> readValue(json, ref);
        } catch (IOException e) {
            logger.info("failed to deserialize filter string " + json, e);
            return Collections.emptyList();
        }
    }

    /**
     * turn filter parameters or sort parameters into a map
     * 
     * @param json
     * @param keyProp
     * @param valueProp
     * @return
     */
    private List<Sorter> parseSorters(String json) {
        if (!StringUtils.hasLength(json)) {
            return Collections.emptyList();
        }// [{"property":"name","direction":"ASC"}]
        try {
            TypeReference<List<Sorter>> ref = new TypeReference<List<Sorter>>() {
            };
            return objectMapper.<List<Sorter>> readValue(json, ref);
        } catch (IOException e) {
            logger.info("failed to deserialize sorter string " + json, e);
            return Collections.emptyList();
        }
    }
}
