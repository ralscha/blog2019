import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {TabsPage} from './tabs.page';

const routes: Routes = [
  {
    path: 'tabs',
    component: TabsPage,
    children: [
      {
        path: 'mobilenet',
        children: [
          {
            path: '',
            loadChildren: '../mobilenet/mobilenet.module#MobilenetPageModule'
          }
        ]
      },
      {
        path: 'objectdetection',
        children: [
          {
            path: '',
            loadChildren: '../object-detection/object-detection.module#ObjectDetectionPageModule'
          }
        ]
      },
      {
        path: 'posenet',
        children: [
          {
            path: '',
            loadChildren: '../posenet/posenet.module#PosenetPageModule'
          }
        ]
      },
      {
        path: 'speech',
        children: [
          {
            path: '',
            loadChildren: '../speech/speech.module#SpeechPageModule'
          }
        ]
      },
      {
        path: '',
        redirectTo: '/tabs/mobilenet',
        pathMatch: 'full'
      }
    ]
  },
  {
    path: '',
    redirectTo: '/tabs/mobilenet',
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TabsPageRoutingModule {
}
