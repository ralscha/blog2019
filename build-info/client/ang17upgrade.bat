call npm install
call ng update @angular/core@17 @angular/cli@17 @angular-eslint/schematics --allow-dirty --force
call ng update @angular/cdk@17 --allow-dirty --force
call ng update @angular/material@17 --allow-dirty --force
call ncu -u
sed -i "s/[\^]//g" package.json
sed -i "s/[\"]typescript[\"]: [\"]5.3.2[\"]/\"typescript\": \"5.2.2\"/g" package.json
rm node_modules package-lock.json -fr
call npm install
call ng lint