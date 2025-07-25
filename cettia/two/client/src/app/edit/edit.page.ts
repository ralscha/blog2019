import {Component, inject, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {TodoService} from '../todo.service';
import {v4 as uuid} from 'uuid';
import {FormsModule} from '@angular/forms';
import {
  IonBackButton,
  IonButton,
  IonButtons,
  IonCheckbox,
  IonContent,
  IonFooter,
  IonHeader,
  IonIcon,
  IonInput,
  IonItem,
  IonList,
  IonTitle,
  IonToolbar,
  NavController
} from "@ionic/angular/standalone";
import {addIcons} from "ionicons";
import {trashOutline} from "ionicons/icons";
import {Todo} from "../protos/changeevent";

@Component({
  selector: 'app-edit',
  templateUrl: './edit.page.html',
  styleUrls: ['./edit.page.scss'],
  imports: [FormsModule, IonHeader, IonToolbar, IonButtons, IonBackButton, IonTitle, IonButton, IonIcon, IonContent, IonList, IonItem, IonInput, IonCheckbox, IonFooter]
})
export class EditPage implements OnInit {
  todo: Todo | undefined;
  private readonly navCtrl = inject(NavController);
  private readonly route = inject(ActivatedRoute);
  private readonly todoService = inject(TodoService);

  constructor() {
    addIcons({trashOutline});
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.todo = this.todoService.getTodo(id);
    } else {
      this.todo = {
        id: uuid(),
        title: '',
        completed: false
      };
    }
  }

  save(): void {
    if (this.todo) {
      this.todoService.save(this.todo);
    }
    this.navCtrl.navigateBack(['home']);
  }

  deleteTodo(): void {
    if (this.todo) {
      this.todoService.deleteTodo(this.todo);
    }
    this.navCtrl.navigateBack(['home']);
  }


}
