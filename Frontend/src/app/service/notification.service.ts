import { Injectable } from '@angular/core';
import {NotifierService} from 'angular-notifier';
import {NotificationEnum} from '../enum/notification-enum.enum';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private notifier: NotifierService;

  constructor(notifier:NotifierService) { }

  public notify(type:NotificationEnum, message:string){
    this.notifier.notify(type, message);
  }
}
