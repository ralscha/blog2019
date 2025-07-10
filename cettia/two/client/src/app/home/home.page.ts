import {Component, inject} from '@angular/core';
import {TodoService} from '../todo.service';
import {AsyncPipe} from '@angular/common';
import {
  IonButton,
  IonButtons,
  IonContent,
  IonHeader,
  IonIcon,
  IonItem,
  IonLabel,
  IonList,
  IonTitle,
  IonToolbar,
  NavController
} from "@ionic/angular/standalone";
import {addIcons} from "ionicons";
import {addOutline} from "ionicons/icons";
import {Todo} from "../protos/changeevent";

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
  imports: [AsyncPipe, IonHeader, IonToolbar, IonButtons, IonContent, IonItem, IonList, IonButton, IonTitle, IonIcon, IonLabel]
})
export class HomePage {
  readonly todoService = inject(TodoService);
  private readonly navCtrl = inject(NavController);

  constructor() {
    addIcons({addOutline});
  }

  addTodo(): void {
    this.navCtrl.navigateForward(['edit']);
  }

  editTodo(todo: Todo): void {
    this.navCtrl.navigateForward(['edit', todo.id]);
  }

}
