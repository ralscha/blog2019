import { Component, input, ChangeDetectionStrategy } from '@angular/core';

@Component({
  selector: 'app-progress-bar',
  templateUrl: './progress-bar.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './progress-bar.component.scss',
})
export class ProgressBarComponent {
  readonly progress = input.required<number>();
}
