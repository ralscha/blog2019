const fs = require('node:fs/promises');
const path = require('node:path');
const packageJson = require('./package.json');
const {simpleGit} = require('simple-git');

const environmentProdPath = path.join(__dirname, 'src', 'environments',
    'environment.prod.ts');

function replaceEnvironmentValue(content, key, value) {
  return content.replace(new RegExp(`(${key}: )([^\n]+?)(,?)$`, 'm'),
      (_, prefix, __, trailingComma) => `${prefix}${value}${trailingComma}`);
}

async function main() {
  const git = simpleGit(__dirname);
  const log = await git.log({maxCount: 1});
  const latestCommit = log.latest;

  if (!latestCommit) {
    throw new Error('Unable to read the latest Git commit');
  }

  let environmentFile = await fs.readFile(environmentProdPath, 'utf8');
  environmentFile = replaceEnvironmentValue(environmentFile, 'version',
      `'${packageJson.version}'`);
  environmentFile = replaceEnvironmentValue(environmentFile, 'buildTimestamp',
      Math.floor(Date.now() / 1000));
  environmentFile = replaceEnvironmentValue(environmentFile, 'shortCommitId',
      `'${latestCommit.hash.slice(0, 7)}'`);
  environmentFile = replaceEnvironmentValue(environmentFile, 'commitId',
      `'${latestCommit.hash}'`);
  environmentFile = replaceEnvironmentValue(environmentFile, 'commitTime',
      Math.floor(new Date(latestCommit.date).getTime() / 1000));

  await fs.writeFile(environmentProdPath, environmentFile);
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
