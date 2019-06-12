const replaceInFile = require('replace-in-file');
const packageJson = require('./package.json');
const packageVersion = packageJson.version;
const simpleGit = require('simple-git')();

replaceInFile.sync({
    files: './src/environments/environment.prod.ts',
    from: [/version: '.+'/, /buildTimestamp: \d+/],
	to: [`version: '${packageVersion}'`, `buildTimestamp: ${Math.floor(Date.now() / 1000)}`]
});

simpleGit.log(['--pretty=format:%h,%H,%at', '-n', '1'], (err, log) => handleGitLog(log, err));

function handleGitLog(log, err) {
  if (!err) {
    const response = log.latest.hash;
    const splitted = response.split(',');
    const gitInfo = {
      shortCommitId: splitted[0],
      commitId: splitted[1],
      commitTime: parseInt(splitted[2])
    };

    replaceInFile.sync({
      files: './src/environments/environment.prod.ts',
      from: [/shortCommitId: '.+'/, /commitId: '.+'/, /commitTime: \d+/],
      to: [`shortCommitId: '${splitted[0]}'`, `commitId: '${splitted[1]}'`, `commitTime: ${splitted[2]}`]
    });

  } else {
    console.log(err);
  }
}
