import {Component} from '@angular/core';
import {NavController} from '@ionic/angular';
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
  IonToolbar
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

  constructor(private readonly navCtrl: NavController,
              readonly todoService: TodoService) {
    addIcons({addOutline});
  }

  addTodo(): void {
    this.navCtrl.navigateForward(['edit']);
  }

  editTodo(todo: Todo): void {
    this.navCtrl.navigateForward(['edit', todo.id]);
  }

}
