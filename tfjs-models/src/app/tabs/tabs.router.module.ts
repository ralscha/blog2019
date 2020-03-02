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
            loadChildren: () => import('../mobilenet/mobilenet.module').then(m => m.MobilenetPageModule)
          }
        ]
      },
      {
        path: 'objectdetection',
        children: [
          {
            path: '',
            loadChildren: () => import('../object-detection/object-detection.module').then(m => m.ObjectDetectionPageModule)
          }
        ]
      },
      {
        path: 'posenet',
        children: [
          {
            path: '',
            loadChildren: () => import('../posenet/posenet.module').then(m => m.PosenetPageModule)
          }
        ]
      },
      {
        path: 'speech',
        children: [
          {
            path: '',
            loadChildren: () => import('../speech/speech.module').then(m => m.SpeechPageModule)
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
