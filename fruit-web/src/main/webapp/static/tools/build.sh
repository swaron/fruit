
basedir=$(dirname $0)

cd ${basedir}
if [ -x "/usr/local/bin/node" ]
then
/usr/local/bin/node r.js -o app.build.js
/usr/local/bin/lessc -x -ru --source-map ../libs/bootstrap/3.2.0/less/bootstrap.less ../js/bootstrap/3.2.0/less/bootstrap.min.css
/usr/local/bin/lessc -x -ru --source-map ../libs/bootstrap/3.2.0/less/theme.less ../js/bootstrap/3.2.0/less/bootstrap-theme.min.css
/usr/local/bin/lessc -x -ru --source-map ../css/app.less ../css/app.min.css
/usr/local/bin/lessc -x -ru --source-map ../css/markdown.less ../css/markdown.min.css
else
node r.js -o app.build.js 
lessc -x -ru --source-map ../libs/bootstrap/3.2.0/less/bootstrap.less ../js/bootstrap/3.2.0/less/bootstrap.min.css
lessc -x -ru --source-map ../libs/bootstrap/3.2.0/less/theme.less ../js/bootstrap/3.2.0/less/bootstrap-theme.min.css
lessc -x -ru --source-map ../css/app.less ../css/app.min.css
lessc -x -ru --source-map ../css/markdown.less ../css/markdown.min.css
fi
