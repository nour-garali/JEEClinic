import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import Swal, { SweetAlertIcon } from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class AlertService {

  constructor(private toastr: ToastrService) { }

  // --- Toastr Notifications (Transient) ---

  success(message: string, title: string = 'Succès') {
    this.toastr.success(message, title);
  }

  error(message: string, title: string = 'Erreur') {
    this.toastr.error(message, title);
  }

  info(message: string, title: string = 'Information') {
    this.toastr.info(message, title);
  }

  warning(message: string, title: string = 'Attention') {
    this.toastr.warning(message, title);
  }

  // --- SweetAlert2 Dialogs (Modal) ---

  async alert(title: string, text: string, icon: SweetAlertIcon = 'info') {
    return Swal.fire({
      title,
      text,
      icon,
      confirmButtonColor: '#3085d6'
    });
  }

  async confirm(title: string, text: string, confirmButtonText: string = 'Oui', cancelButtonText: string = 'Annuler') {
    return Swal.fire({
      title,
      text,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText,
      cancelButtonText
    });
  }

  async toast(title: string, icon: SweetAlertIcon = 'success') {
    const Toast = Swal.mixin({
      toast: true,
      position: 'top-end',
      showConfirmButton: false,
      timer: 3000,
      timerProgressBar: true,
      didOpen: (toast) => {
        toast.addEventListener('mouseenter', Swal.stopTimer)
        toast.addEventListener('mouseleave', Swal.resumeTimer)
      }
    });

    return Toast.fire({
      icon,
      title
    });
  }
}
