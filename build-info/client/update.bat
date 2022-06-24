call npm install
call ng update @angular/core @angular/cli @angular-eslint/schematics --allow-dirty
call ncu -u
rm -fr node_modules package-lock.json .angular dist
sed -i "s/\"\: \"\^/\": \"/g" package.json
sed -i "s/\"\: \"\~/\": \"/g" package.json
sed -i "/^    \"@types\/node\"\: \"18\.0\.0\",/d" package.json
call npm install
call npm run build
call ng lint
rm -fr node_modules package-lock.json .angular dist
