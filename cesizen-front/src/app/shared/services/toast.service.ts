import { ApplicationRef, ComponentRef, Injectable, createComponent } from '@angular/core';
import { ToastComponent } from '../components/toast/toast.component';

@Injectable({ providedIn: 'root' })
export class ToastService {
  constructor(private appRef: ApplicationRef) {}

  private show(message: string, type: 'success' | 'error' = 'success', duration = 3000) {
    const compRef: ComponentRef<ToastComponent> = createComponent(ToastComponent, {
      environmentInjector: this.appRef.injector
    });

    compRef.instance.message = message;
    compRef.instance.type = type;

    this.appRef.attachView(compRef.hostView);
    const domNode = (compRef.hostView as any).rootNodes[0] as HTMLElement;
    document.body.appendChild(domNode);

    setTimeout(() => {
      try {
        this.appRef.detachView(compRef.hostView);
        compRef.destroy();
      } catch (_) {}
    }, duration);
  }

  success(message: string, duration?: number) {
    this.show(message, 'success', duration);
  }

  error(message: string, duration?: number) {
    this.show(message, 'error', duration);
  }
}
