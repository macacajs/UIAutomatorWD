git_version = $$(git branch 2>/dev/null | sed -e '/^[^*]/d'-e's/* \(.*\)/\1/')
npm_bin= $$(npm bin)

all: test
install:
	@npm install
travis: install
jshint:
	@${npm_bin}/jshint .
build:
	@node ./scripts/build.js
.PHONY: test
