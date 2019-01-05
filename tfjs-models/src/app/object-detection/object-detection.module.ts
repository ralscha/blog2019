import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {RouterModule, Routes} from '@angular/router';

import {IonicModule} from '@ionic/angular';

import {ObjectDetectionPage} from './object-detection.page';

const routes: Routes = [
  {
    path: '',
    component: ObjectDetectionPage
  }
];

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    RouterModule.forChild(routes)
  ],
  declarations: [ObjectDetectionPage]
})
export class ObjectDetectionPageModule {
}
