import {Component, DOCUMENT, HostListener, Inject, PLATFORM_ID} from '@angular/core';
import {isPlatformBrowser, NgOptimizedImage} from '@angular/common';
import {MatButton, MatIconButton} from '@angular/material/button';
import {RouterLink} from '@angular/router';
import {MatIconModule} from '@angular/material/icon';

@Component({
  selector: 'app-header',
  imports: [
    MatIconButton,
    MatButton,
    NgOptimizedImage,
    RouterLink,
    MatIconModule
  ],
  templateUrl: './header.html',
  styleUrl: './header.scss'
})
export class Header {
  isMenuOpen = false;
  private readonly isBrowser: boolean;

  constructor(
    @Inject(PLATFORM_ID) platformId: Object,
    @Inject(DOCUMENT) private doc: Document
  ) {
    this.isBrowser = isPlatformBrowser(platformId);
  }

  toggleMenu(): void {
    this.isMenuOpen ? this.closeMenu() : this.openMenu();
  }

  openMenu(): void {
    this.isMenuOpen = true;
    this.lockScroll();
  }

  closeMenu(): void {
    this.isMenuOpen = false;
    this.unlockScroll();
  }

  @HostListener('document:keydown.escape')
  onEsc(): void {
    if (this.isMenuOpen) this.closeMenu();
  }

  private lockScroll(): void {
    if (!this.isBrowser) return;
    this.doc.body.style.overflow = 'hidden';
  }

  private unlockScroll(): void {
    if (!this.isBrowser) return;
    this.doc.body.style.overflow = '';
  }
}
