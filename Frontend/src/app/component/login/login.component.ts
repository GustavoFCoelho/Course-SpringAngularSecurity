import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {AuthenticationService} from '../../service/authentication.service';
import {NotifierService} from 'angular-notifier';
import {User} from '../../model/user';
import {Subscription} from 'rxjs';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {NotificationEnum} from '../../enum/notification-enum.enum';
import {HeaderType} from '../../enum/heade-typer.enum';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {
  showLoading: Boolean = false;
  private subscription:Subscription[] = [];


  constructor(private router:Router, private authService:AuthenticationService, private notifier:NotifierService) { }

  ngOnInit(): void {
    if(this.authService.isLoggedIn()){
      this.router.navigateByUrl("/user/management")
    } else {
      this.router.navigateByUrl('/login')
    }
  }

  public onLogin(user:User): void{
    this.showLoading = true;
    this.subscription.push(this.authService.login(user).subscribe(
      (response:HttpResponse<User>) =>{
        const token = response.headers.get(HeaderType.JWT_TOKEN);
        this.authService.saveToken(token);
        this.authService.addUserToLocalCache(response.body);
        this.router.navigateByUrl("/user/management");
        this.showLoading = false;
      }
    , (error:HttpErrorResponse) =>{
        console.log(error);
        this.sendErrorNotification(error.error.message);
    }));
  }

  ngOnDestroy(): void {
    this.subscription.forEach(sub => sub.unsubscribe());
  }

  private sendErrorNotification(message: any) {
    if(message){
      this.notifier.notify(NotificationEnum.ERROR, message);
    } else {
      this.notifier.notify(NotificationEnum.ERROR, "An Error Ocurred");
    }
  }
}
