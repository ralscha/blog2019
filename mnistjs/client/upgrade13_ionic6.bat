call npm install --legacy-peer-deps
call npx @angular/cli@13 update @angular/core@13 @angular/cli@13 --allow-dirty --force
call npm install @ionic/angular
call ncu -u
call cp e:\upgrade_a13\.browserslistrc .
call cp e:\upgrade_a13\.eslintrc.json .
call cp e:\upgrade_a13\.gitignore .
call cp e:\upgrade_a13\polyfills.ts src
call cp e:\upgrade_a13\tsconfig.json .
call cp e:\upgrade_a13\zone-flags.ts src
call rm node_modules package-lock.json -fr
call npm install --legacy-peer-deps
call ng lint --fix
call npm run build

