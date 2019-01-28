import {Component} from '@angular/core';
import {NavController} from '@ionic/angular';
import {TodoService} from '../todo.service';
import {ITodo} from '../protos/changeevent';

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss']
})
export class HomePage {

  constructor(private readonly navCtrl: NavController,
              readonly todoService: TodoService) {
  }

  addTodo() {
    this.navCtrl.navigateForward(['edit']);
  }

  editTodo(todo: ITodo) {
    this.navCtrl.navigateForward(['edit', todo.id]);
  }

}
