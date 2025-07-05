import {Component, inject, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {forkJoin, Observable} from 'rxjs';
import {environment} from '../environments/environment';
import {BuildInfo} from './model/build-info';
import {GitInfo} from './model/git-info';
import {ProfileInfo} from './model/profile-info';
import {ClientInfo} from './model/client-info';
import {AsyncPipe, DatePipe} from "@angular/common";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  imports: [
    AsyncPipe,
    DatePipe
  ]
})
export class AppComponent implements OnInit {
  info$!: Observable<{ build: BuildInfo, git: GitInfo, profile: ProfileInfo }>;
  clientInfo: ClientInfo;
  private readonly httpClient = inject(HttpClient);

  constructor() {
    this.clientInfo = {
      version: environment.version,
      buildTimestamp: environment.buildTimestamp ? environment.buildTimestamp * 1000 : null,
      shortCommitId: environment.shortCommitId,
      commitId: environment.commitId,
      commitTime: environment.commitTime ? environment.commitTime * 1000 : null
    };
  }

  ngOnInit(): void {
    this.info$ = forkJoin({
      build: this.httpClient.get<BuildInfo>(`${environment.serverURL}/build-info`),
      git: this.httpClient.get<GitInfo>(`${environment.serverURL}/git-info`),
      profile: this.httpClient.get<ProfileInfo>(`${environment.serverURL}/profile-info`)
    });
  }

}
