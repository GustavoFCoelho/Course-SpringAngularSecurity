import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {AuthenticationService} from '../../service/authentication.service';
import {NotifierService} from 'angular-notifier';
import {User} from '../../model/user';
import {Subscription} from 'rxjs';
import {HttpResponse} from '@angular/common/http';

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
        const token = response.headers.get('Jwt-Token');
        this.authService.saveToken(token);
        this.authService.addUserToLocalCache(response.body);
        this.router.navigateByUrl("/user/management");
        this.showLoading = false;
      }
    ));
  }

  ngOnDestroy(): void {

  }

}
