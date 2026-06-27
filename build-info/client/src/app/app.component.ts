import { DatePipe } from '@angular/common';
import { httpResource } from '@angular/common/http';
import { Component, computed } from '@angular/core';
import { environment } from '../environments/environment';
import { BuildInfo } from './model/build-info';
import { ClientInfo } from './model/client-info';
import { GitInfo } from './model/git-info';
import { ProfileInfo } from './model/profile-info';

@Component({
  selector: 'app-root',
  standalone: true,
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  imports: [DatePipe],
})
export class AppComponent {
  readonly clientInfo: ClientInfo = {
    version: environment.version,
    buildTimestamp: environment.buildTimestamp ? environment.buildTimestamp * 1000 : null,
    shortCommitId: environment.shortCommitId,
    commitId: environment.commitId,
    commitTime: environment.commitTime ? environment.commitTime * 1000 : null,
  };

  private readonly buildInfo = httpResource<BuildInfo>(() => `${environment.serverURL}/build-info`);
  private readonly gitInfo = httpResource<GitInfo>(() => `${environment.serverURL}/git-info`);
  private readonly profileInfo = httpResource<ProfileInfo>(
    () => `${environment.serverURL}/profile-info`,
  );

  readonly info = computed<{ build: BuildInfo; git: GitInfo; profile: ProfileInfo } | null>(() => {
    if (!this.buildInfo.hasValue() || !this.gitInfo.hasValue() || !this.profileInfo.hasValue()) {
      return null;
    }

    return {
      build: this.buildInfo.value(),
      git: this.gitInfo.value(),
      profile: this.profileInfo.value(),
    };
  });
}
