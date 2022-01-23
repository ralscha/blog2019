.PHONY: check-dep
check-dep:
	cd ./bigintjson && ./mvnw.cmd versions:display-dependency-updates && ./mvnw.cmd versions:display-plugin-updates
	cd ./build-info/client && ncu
	cd ./build-info/server && ./mvnw.cmd versions:display-dependency-updates && ./mvnw.cmd versions:display-plugin-updates
	cd ./capupload/client && ncu
	cd ./capupload/server && ./mvnw.cmd versions:display-dependency-updates && ./mvnw.cmd versions:display-plugin-updates
	cd ./catch-all-smtp && ./mvnw.cmd versions:display-dependency-updates && ./mvnw.cmd versions:display-plugin-updates
	cd ./cettia/one/client && ncu
	cd ./cettia/one/server && ./mvnw.cmd versions:display-dependency-updates && ./mvnw.cmd versions:display-plugin-updates
	cd ./cettia/simple && ./mvnw.cmd versions:display-dependency-updates && ./mvnw.cmd versions:display-plugin-updates
	cd ./cettia/two/client && ncu
	cd ./cettia/two/server && ./mvnw.cmd versions:display-dependency-updates && ./mvnw.cmd versions:display-plugin-updates
	cd ./credential/client && ncu
	cd ./credential/server && ./mvnw.cmd versions:display-dependency-updates && ./mvnw.cmd versions:display-plugin-updates
	cd ./envers && ./mvnw.cmd versions:display-dependency-updates && ./mvnw.cmd versions:display-plugin-updates
	cd ./fa && ncu
	cd ./googlefont && ncu
	cd ./java11httpclient/client && ./mvnw.cmd versions:display-dependency-updates && ./mvnw.cmd versions:display-plugin-updates
	cd ./java11httpclient/server && ./mvnw.cmd versions:display-dependency-updates && ./mvnw.cmd versions:display-plugin-updates
	cd ./jgit/examples && ./mvnw.cmd versions:display-dependency-updates && ./mvnw.cmd versions:display-plugin-updates
	cd ./jgit/githubbackup && ./mvnw.cmd versions:display-dependency-updates && ./mvnw.cmd versions:display-plugin-updates
	cd ./ky/client && ncu
	cd ./ky/server && ./mvnw.cmd versions:display-dependency-updates && ./mvnw.cmd versions:display-plugin-updates
	cd ./mnistjs/client && ncu
	cd ./mnistjs/train && ncu
	cd ./ratelimit && ./mvnw.cmd versions:display-dependency-updates && ./mvnw.cmd versions:display-plugin-updates
	cd ./sbjooqflyway && ./mvnw.cmd versions:display-dependency-updates && ./mvnw.cmd versions:display-plugin-updates
	cd ./stateless/client && ncu
	cd ./stateless/server && ./mvnw.cmd versions:display-dependency-updates && ./mvnw.cmd versions:display-plugin-updates
	cd ./tfjs-models && ncu
	cd ./tls-and-h2 && ./mvnw.cmd versions:display-dependency-updates && ./mvnw.cmd versions:display-plugin-updates
	cd ./uploadtus/client && ncu
	cd ./uploadtus/server && ./mvnw.cmd versions:display-dependency-updates && ./mvnw.cmd versions:display-plugin-updates
	cd ./visibility/client/simple && ncu
	cd ./visibility/client/visibility.js && ncu
	cd ./webocr && ncu
	cd ./webpush && ./mvnw.cmd versions:display-dependency-updates && ./mvnw.cmd versions:display-plugin-updates
	cd ./webworkers/angular && ncu
	cd ./webworkers/angular-comlink && ncu
	cd ./webworkers/basic && ncu
	cd ./webworkers/earthquakes && ncu
	cd ./webworkers/mandel-with-worker && ncu
	cd ./webworkers/parceljs-comlink && ncu
