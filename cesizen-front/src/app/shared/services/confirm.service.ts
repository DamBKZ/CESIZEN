import { ApplicationRef, ComponentRef, Injectable, Injector, createComponent } from '@angular/core';
import { ConfirmComponent } from '../components/confirm/confirm.component';

@Injectable({ providedIn: 'root' })
export class ConfirmService {
  constructor(private injector: Injector, private appRef: ApplicationRef) {}

  confirm(message: string): Promise<boolean> {
    return new Promise((resolve) => {
      const compRef: ComponentRef<ConfirmComponent> = createComponent(ConfirmComponent, {
        environmentInjector: this.appRef.injector
      });

      compRef.instance.message = message;
      const sub = compRef.instance.result.subscribe((v: boolean) => {
        resolve(v);
        sub.unsubscribe();
        this.appRef.detachView(compRef.hostView);
        compRef.destroy();
      });

      this.appRef.attachView(compRef.hostView);
      const domNode = (compRef.hostView as any).rootNodes[0] as HTMLElement;
      document.body.appendChild(domNode);
    });
  }
}
