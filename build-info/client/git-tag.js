const packageJson = require('./package.json');
const packageVersion = packageJson.version;
const simpleGit = require('simple-git')();

simpleGit
     .add('./*')
     .commit(`release v${packageVersion}`)
     .tag(['-a', `v${packageVersion}`, '-m', `release v${packageVersion}`]);
	 //.push('origin', 'master');