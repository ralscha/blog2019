import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {RouterModule, Routes} from '@angular/router';
import {IonicModule} from '@ionic/angular';
import {SignupPage} from './signup.page';
import {SignupSecretPage} from '../signup-secret/signup-secret.page';
import {SignupOkayPage} from '../signup-okay/signup-okay.page';

const routes: Routes = [
  {
    path: '',
    component: SignupPage
  },
  {
    path: 'secrect',
    component: SignupSecretPage
  },
  {
    path: 'okay',
    component: SignupOkayPage
  }
];

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    RouterModule.forChild(routes)
  ],
  declarations: [SignupPage, SignupSecretPage, SignupOkayPage]
})
export class SignupPageModule {
}
