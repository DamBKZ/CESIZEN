import { ApplicationRef, ComponentRef, Injectable, createComponent, inject } from '@angular/core';

import {
  ToastComponent,
  ToastType
} from '../components/toast/toast.component';

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private readonly appRef = inject(ApplicationRef);


  success(
    message: string,
    duration = 3000
  ): void {
    this.show(message, 'success', duration);
  }

  error(
    message: string,
    duration = 3000
  ): void {
    this.show(message, 'error', duration);
  }

  info(
    message: string,
    duration = 3000
  ): void {
    this.show(message, 'info', duration);
  }

  private show(
    message: string,
    type: ToastType,
    duration: number
  ): void {
    const normalizedMessage = message.trim();

    if (!normalizedMessage) {
      return;
    }

    const componentRef: ComponentRef<ToastComponent> =
      createComponent(ToastComponent, {
        environmentInjector: this.appRef.injector
      });

    componentRef.instance.message = normalizedMessage;
    componentRef.instance.type = type;

    this.appRef.attachView(componentRef.hostView);

    const hostElement =
      componentRef.location.nativeElement as HTMLElement;

    document.body.appendChild(hostElement);

    window.setTimeout(
      () => this.destroyComponent(componentRef),
      Math.max(0, duration)
    );
  }

  private destroyComponent(
    componentRef: ComponentRef<ToastComponent>
  ): void {
    const hostElement =
      componentRef.location.nativeElement as HTMLElement;

    try {
      this.appRef.detachView(componentRef.hostView);

      if (hostElement.isConnected) {
        hostElement.remove();
      }
    } finally {
      componentRef.destroy();
    }
  }
}
