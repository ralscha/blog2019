import {AsyncPipe, DatePipe} from '@angular/common';
import {HttpClient} from '@angular/common/http';
import {Component, inject} from '@angular/core';
import {forkJoin, Observable} from 'rxjs';
import {environment} from '../environments/environment';
import {BuildInfo} from './model/build-info';
import {ClientInfo} from './model/client-info';
import {GitInfo} from './model/git-info';
import {ProfileInfo} from './model/profile-info';

@Component({
  selector: 'app-root',
  standalone: true,
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  imports: [
    AsyncPipe,
    DatePipe
  ]
})
export class AppComponent {
  private readonly httpClient = inject(HttpClient);

  readonly clientInfo: ClientInfo = {
    version: environment.version,
    buildTimestamp: environment.buildTimestamp ? environment.buildTimestamp * 1000 : null,
    shortCommitId: environment.shortCommitId,
    commitId: environment.commitId,
    commitTime: environment.commitTime ? environment.commitTime * 1000 : null
  };

  readonly info$: Observable<{ build: BuildInfo, git: GitInfo, profile: ProfileInfo }> =
      forkJoin({
        build: this.httpClient.get<BuildInfo>(`${environment.serverURL}/build-info`),
        git: this.httpClient.get<GitInfo>(`${environment.serverURL}/git-info`),
        profile: this.httpClient.get<ProfileInfo>(`${environment.serverURL}/profile-info`)
      });

}
