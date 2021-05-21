call npm install --legacy-peer-deps
call ng update @angular/core@12 @angular/cli@12 --allow-dirty --force
call ng add @angular-eslint/schematics --skip-confirmation
call ng g @angular-eslint/schematics:convert-tslint-to-eslint --remove-tslint-if-no-more-tslint-targets --ignore-existing-tslint-config
cp E:\.eslintrc.json .
call ncu -u
call rm node_modules package-lock.json -fr
call npm install --legacy-peer-deps
call ng lint -- --fix
call npm run build-prod

