import { ApplicationRef, ComponentRef, Injectable, EmbeddedViewRef, createComponent, inject } from '@angular/core';
import { ConfirmComponent } from '../components/confirm/confirm.component';

@Injectable({ providedIn: 'root' })
export class ConfirmService {
  private readonly appRef = inject(ApplicationRef);


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
const hostView = compRef.hostView as EmbeddedViewRef<unknown>;
const domNode = hostView.rootNodes[0] as HTMLElement;
      document.body.appendChild(domNode);
    });
  }
}
