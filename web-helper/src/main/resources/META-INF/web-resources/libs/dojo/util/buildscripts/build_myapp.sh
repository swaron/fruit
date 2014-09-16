#!/bin/bash

basedir=$(dirname $0)
cd ${basedir}
${basedir}/build.sh -p myapp -r
