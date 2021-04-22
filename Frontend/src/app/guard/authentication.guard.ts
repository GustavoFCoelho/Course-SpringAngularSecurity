import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import {Observable} from 'rxjs';
import {AuthenticationService} from '../service/authentication.service';
import {NotificationService} from '../service/notification.service';
import {NotificationEnum} from '../enum/notification-enum.enum';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationGuard implements CanActivate {

  constructor(private authService:AuthenticationService, private router: Router, private notificationService:NotificationService) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.isUserLoggedIn();
  }

  private isUserLoggedIn(): boolean {
    if(this.authService.isLoggedIn()){
      return true;
    }
    this.router.navigate(["/login"])
    this.notificationService.notify(NotificationEnum.ERROR, "YOU NEED TO LOG IN TO ACCESS THIS PAGE");
    return false;
  }
}
