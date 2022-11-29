call npm install --force
call ng update @angular/core@15 @angular/cli@15 @angular-eslint/schematics --allow-dirty --force
call ng update @angular/material@15 --allow-dirty --force
rm src/polyfills.ts
call ncu -u
sed -i "s/[\^]//g" package.json
sed -i "s/4.9.3/4.8.4/g" package.json
cp D:\ws\github\starters\ngblank\.eslintrc.json .eslintrc.json
cp D:\ws\github\starters\ngblank\tsconfig.app.json tsconfig.app.json
cp D:\ws\github\starters\ngblank\tsconfig.json tsconfig.json
cp D:\ws\github\starters\ngblank\.browserslistrc .browserslistrc
cp D:\ws\github\starters\ngblank\src\main.ts src\main.ts
cp D:\ws\github\starters\ngblank\angular.json angular.json
rm node_modules package-lock.json -fr
call npm install --force
call ng lint
