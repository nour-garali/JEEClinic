import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="prohealth-wrapper">
      
      <!-- HERO SECTION -->
      <header class="hero-header">
        <div class="hero-bg-overlay"></div>
        <div class="hero-content-wrapper">
          
          <!-- Navbar -->
          <nav class="navbar">
            <div class="logo">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                <circle cx="12" cy="10" r="3" fill="white"/>
              </svg>
              <span class="logo-text">ProHealth</span>
            </div>
            
            <ul class="nav-menu">
              <li><a href="#" class="active">Home</a></li>
              <li><a href="#">About</a></li>
              <li><a href="#">Find Doctor</a></li>
              <li><a href="#">Blog</a></li>
              <li><a href="#">Contact</a></li>
            </ul>
            
            <div class="nav-actions">
              <a routerLink="/login" class="nav-btn login-btn">Se connecter</a>
              <a routerLink="/register" class="nav-btn register-btn">S'inscrire</a>
            </div>
          </nav>

          <!-- Floating card -->
          <div class="floating-patient-card">
            <div class="avatars-group">
              <div class="avatar"><img src="/team-img.png" alt="P1"></div>
              <div class="avatar"><img src="/team-img.png" alt="P2"></div>
              <div class="avatar"><img src="/team-img.png" alt="P3"></div>
            </div>
            <div class="pr-inner">
              <div class="pr-number">150K +</div>
              <div class="pr-label">Patient Recover</div>
            </div>
            <div class="pr-check">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="white">
                <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
              </svg>
            </div>
          </div>

          <!-- Hero Main Content -->
          <div class="hero-body">
            <h1 class="hero-title">Compassionate care,<br>exceptional results.</h1>
            
            <div class="hero-subtitle">
              <div class="subtitle-brand">
                <span>Pro Health</span>
                <div class="subtitle-line"></div>
              </div>
              <p>Our team of experienced doctors and healthcare<br>professionals are committed to providing quality care and<br>personalized attention to our patients.</p>
            </div>
            
            <button class="play-btn-group">
              <div class="play-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="white">
                  <path d="M8 5v14l11-7z"/>
                </svg>
              </div>
              <span class="play-text">See how we work</span>
            </button>
          </div>

          <!-- Glass Stats Banner -->
          <div class="stats-banner">
            <div class="stat-item">
              <h3>20+</h3>
              <p>Years of experience</p>
            </div>
             <div class="stat-item">
              <h3>95%</h3>
              <p>Patient satisfaction rating</p>
            </div>
             <div class="stat-item">
              <h3>5000+</h3>
              <p>Patients served annually</p>
            </div>
             <div class="stat-item">
              <h3>10+</h3>
              <p>Healthcare providers on staff</p>
            </div>
          </div>

        </div>
      </header>

      <div class="main-content-bg">
        <!-- ABOUT US SECTION -->
        <section class="container split-section">
          <div class="text-side">
            <h5 class="section-tag">ABOUT US</h5>
            <h2 class="section-title">ProHealth is a team<br>of experienced<br>medical professional</h2>
            <p class="section-desc">Dedicated to providing top-quality healthcare services. We believe<br>in a holistic approach to healthcare that focuses on treating the<br>whole person, not just the illness or symptoms.</p>
          </div>
          <div class="image-side">
            <img src="/team-img.png" class="about-image" alt="Medical staff working">
          </div>
        </section>

        <!-- DEPARTMENTS SECTION -->
        <section class="container dep-section">
          <h5 class="section-tag">OUR DEPARTMENTS</h5>
          <h2 class="section-title">For Your Health</h2>

          <div class="dep-grid">
            
            <!-- Card 1 -->
            <div class="dep-card">
              <div class="dep-icon-box">
                <svg viewBox="0 0 24 24" fill="none" stroke="#3b7d9b" stroke-width="1.5">
                  <path d="M5 22h14M12 2v6M12 11v8M10 2h4M9 11h6c1.1 0 2 .9 2 2v6H7v-6c0-1.1.9-2 2-2zM5 13H3M21 13h-2"/>
                </svg>
              </div>
              <div class="dep-card-text">Emergency<br>Department</div>
            </div>

            <!-- Card 2 -->
            <div class="dep-card">
              <div class="dep-icon-box">
                <svg viewBox="0 0 24 24" fill="none" stroke="#3b7d9b" stroke-width="1.5">
                  <circle cx="12" cy="10" r="4"/>
                  <path d="M12 2a8 8 0 0 0-8 8v1a2 2 0 0 0 2 2h0a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2h0a2 2 0 0 0 2-2v-1a8 8 0 0 0-8-8z"/>
                  <path d="M7 15c0 2 2 5 5 5s5-3 5-5"/>
                </svg>
              </div>
              <div class="dep-card-text">Pediatric<br>Departement</div>
            </div>

            <!-- Card 3 -->
            <div class="dep-card">
              <div class="dep-icon-box">
                <svg viewBox="0 0 24 24" fill="none" stroke="#3b7d9b" stroke-width="1.5">
                  <path d="M12 18v-6M9 9a3 3 0 1 1 6 0c0 2-3 5-3 5s-3-3-3-5z"/>
                  <circle cx="6" cy="7" r="2"/>
                  <circle cx="18" cy="7" r="2"/>
                  <path d="M7 8c1 3 3 4 5 4s4-1 5-4"/>
                </svg>
              </div>
              <div class="dep-card-text">Obstetrics<br>and<br>Gynecology<br>Department</div>
            </div>

            <!-- Card 4 -->
            <div class="dep-card">
              <div class="dep-icon-box">
                <svg viewBox="0 0 24 24" fill="none" stroke="#3b7d9b" stroke-width="1.5">
                  <path d="M12 5a3 3 0 0 0-3 3v5s0 4 3 4 3-4 3-4V8a3 3 0 0 0-3-3zM9 8a3 3 0 0 0-5 2v3c0 3 2 4 4 4M15 8a3 3 0 0 1 5 2v3c0 3-2 4-4 4"/>
                  <path d="M12 5V2"/>
                </svg>
              </div>
              <div class="dep-card-text">Cardiology<br>Department</div>
            </div>

            <!-- Card 5 -->
            <div class="dep-card">
              <div class="dep-icon-box">
                <svg viewBox="0 0 24 24" fill="none" stroke="#3b7d9b" stroke-width="1.5">
                   <circle cx="12" cy="12" r="3"/>
                   <path d="M12 9l-2-4M12 15l2 4M15 12l4-2M9 12L5 14M14 10l3-3M10 14l-3 3M10 10L7 7M14 14l3 3"/>
                </svg>
              </div>
              <div class="dep-card-text">Neurology<br>Department</div>
            </div>

            <!-- Card 6 -->
            <div class="dep-card">
              <div class="dep-icon-box">
                <svg viewBox="0 0 24 24" fill="none" stroke="#3b7d9b" stroke-width="1.5">
                  <path d="M9.5 5.5a4.5 4.5 0 0 0-1 8.87V17h7v-2.63a4.5 4.5 0 0 0-1-8.87H9.5z"/>
                  <path d="M12 12v3"/>
                  <path d="M12 5V2"/>
                  <path d="M16 8h3"/>
                  <path d="M5 8h3"/>
                </svg>
              </div>
              <div class="dep-card-text">Psychiatry<br>Department</div>
            </div>

          </div>
        </section>
      </div>

    </div>
  `,
  styles: [`
    /* GLOBAL WRAPPER */
    .prohealth-wrapper {
      font-family: 'Inter', 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
      color: #333;
      background: #fdfdfd;
      min-height: 100vh;
      line-height: 1.5;
    }

    * {
      box-sizing: border-box;
      margin: 0;
      padding: 0;
    }

    /* ===== HERO SECTION ===== */
    .hero-header {
      position: relative;
      height: 700px;
      background-image: url('/hero-bg.png');
      background-size: cover;
      background-position: center;
      background-repeat: no-repeat;
    }

    .hero-bg-overlay {
      position: absolute;
      top: 0; left: 0; right: 0; bottom: 0;
      background: linear-gradient(to right, rgba(48,93,122, 1) 0%, rgba(48,93,122, 0.8) 40%, rgba(48,93,122, 0) 100%);
      z-index: 1;
    }

    .hero-content-wrapper {
      position: relative;
      z-index: 2;
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 40px;
      height: 100%;
      display: flex;
      flex-direction: column;
    }

    /* --- Navbar --- */
    .navbar {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 30px 0;
    }

    .logo {
      display: flex;
      align-items: center;
      gap: 10px;
      color: white;
    }
    .logo-text {
      font-size: 1.3rem;
      font-weight: 500;
      letter-spacing: 0.5px;
    }

    .nav-menu {
      display: flex;
      gap: 40px;
      list-style: none;
    }
    .nav-menu a {
      color: rgba(255, 255, 255, 0.7);
      text-decoration: none;
      font-size: 0.95rem;
      transition: color 0.2s;
      display: flex;
      align-items: center;
      gap: 5px;
    }
    .nav-menu a:hover, .nav-menu a.active {
      color: white;
    }
    .arrow {
      font-size: 0.7rem;
      margin-top: 2px;
    }

    .nav-actions {
      display: flex;
      gap: 15px;
      align-items: center;
    }
    .nav-btn {
      text-decoration: none;
      padding: 10px 22px;
      border-radius: 8px;
      font-weight: 600;
      font-size: 0.9rem;
      transition: all 0.2s;
    }
    .login-btn {
      color: white;
      border: 1px solid rgba(255,255,255,0.4);
    }
    .login-btn:hover {
      background: rgba(255,255,255,0.1);
      border-color: white;
    }
    .register-btn {
      background: #3b7d9b;
      color: white;
    }
    .register-btn:hover {
      background: #2e6179;
      transform: translateY(-1px);
    }

    /* --- Hero Body --- */
    .hero-body {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;
      padding-bottom: 50px;
      width: 50%;
    }

    .hero-title {
      font-size: 3.5rem;
      font-weight: 600;
      color: white;
      line-height: 1.15;
      margin-bottom: 30px;
      letter-spacing: -0.5px;
    }

    .hero-subtitle {
      display: flex;
      gap: 30px;
      margin-bottom: 40px;
      align-items: flex-start;
    }
    .subtitle-brand {
      display: flex;
      flex-direction: column;
      gap: 5px;
      margin-top: 5px;
    }
    .subtitle-brand span {
      color: white;
      font-weight: 600;
      font-size: 0.85rem;
      letter-spacing: 0.5px;
    }
    .subtitle-line {
      width: 40px;
      height: 2px;
      background-color: white;
      border-radius: 2px;
    }
    .hero-subtitle p {
      color: rgba(255, 255, 255, 0.85);
      font-size: 0.95rem;
      font-weight: 300;
      line-height: 1.6;
    }

    .play-btn-group {
      display: flex;
      align-items: center;
      gap: 15px;
      background: none;
      border: none;
      cursor: pointer;
      color: white;
      font-size: 1.05rem;
      font-weight: 500;
      padding: 0;
    }
    .play-icon {
      width: 50px;
      height: 50px;
      border-radius: 50%;
      border: 1px solid rgba(255,255,255,0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.2s;
    }
    .play-btn-group:hover .play-icon {
      background: rgba(255,255,255,0.1);
    }

    /* --- Floating Card --- */
    .floating-patient-card {
      position: absolute;
      top: 160px;
      right: 30px;
      background: rgba(255, 255, 255, 0.75);
      backdrop-filter: blur(15px);
      -webkit-backdrop-filter: blur(15px);
      border-radius: 100px;
      padding: 10px 30px 10px 12px;
      display: flex;
      align-items: center;
      gap: 15px;
      box-shadow: 0 15px 35px rgba(0,0,0,0.06);
      z-index: 10;
      border: 1px solid rgba(255, 255, 255, 0.6);
    }
    .avatars-group {
      display: flex;
      margin-right: 5px;
    }
    .avatar {
      width: 46px;
      height: 46px;
      border-radius: 50%;
      border: 2px solid white;
      overflow: hidden;
      margin-left: -16px;
      background: #e0e0e0;
      box-shadow: 0 4px 10px rgba(0,0,0,0.08);
      position: relative;
    }
    .avatar:nth-child(1) {
      margin-left: 0;
      z-index: 3;
    }
    .avatar:nth-child(2) {
      z-index: 2;
    }
    .avatar:nth-child(3) {
      z-index: 1;
    }
    .avatar img {
      width: 100%; height: 100%; object-fit: cover;
    }
    .pr-inner {
      display: flex;
      flex-direction: column;
      margin-right: 15px;
    }
    .pr-number {
      font-weight: 800;
      font-size: 1.35rem;
      color: #1a3c53;
      line-height: 1.1;
    }
    .pr-label {
      font-size: 0.8rem;
      color: #718a9a;
      font-weight: 500;
      margin-top: 2px;
    }
    .pr-check {
      width: 32px;
      height: 32px;
      background: #207db8;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 12px rgba(32, 125, 184, 0.4);
      position: absolute;
      top: -6px;
      right: -6px;
    }
    .pr-check svg {
      width: 16px;
      height: 16px;
      stroke-width: 3;
    }

    /* --- Stats Banner --- */
    .stats-banner {
      width: 100%;
      background: rgba(255, 255, 255, 0.15);
      backdrop-filter: blur(12px);
      border-top: 1px solid rgba(255,255,255,0.2);
      border-radius: 20px 20px 0 0;
      display: flex;
      justify-content: space-between;
      padding: 30px 40px;
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
    }
    .stat-item h3 {
      color: white;
      font-size: 2.2rem;
      font-weight: 600;
      margin-bottom: 5px;
    }
    .stat-item p {
      color: rgba(255,255,255,0.8);
      font-size: 0.85rem;
      max-width: 150px;
      line-height: 1.4;
    }

    /* ===== MAIN SHARED ===== */
    .main-content-bg {
      background: linear-gradient(180deg, #fefefe 0%, #edf4f8 50%, #d8e8f0 100%);
      padding-bottom: 80px;
    }

    .container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 80px 40px 0;
    }

    .section-tag {
      color: #3b7d9b;
      font-size: 0.85rem;
      font-weight: 600;
      letter-spacing: 1px;
      margin-bottom: 10px;
      text-transform: uppercase;
    }
    .section-title {
      font-size: 2.5rem;
      color: #1a3c53;
      font-weight: 600;
      line-height: 1.25;
      margin-bottom: 20px;
    }

    /* ===== ABOUT SECTION ===== */
    .split-section {
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 40px;
    }
    .text-side {
      flex: 1;
    }
    .section-desc {
      color: #6b8493;
      font-size: 0.95rem;
      line-height: 1.6;
    }
    .image-side {
      flex: 1;
      display: flex;
      justify-content: flex-end;
    }
    .about-image {
      width: 100%;
      max-width: 500px;
      border-radius: 20px;
      box-shadow: 0 10px 40px rgba(0,0,0,0.06);
      object-fit: cover;
    }

    /* ===== DEPARTMENTS SECTION ===== */
    .dep-section {
      padding-top: 100px;
    }

    .dep-grid {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 20px;
      margin-top: 40px;
    }

    .dep-card {
      background: white;
      border: 1.5px solid #d6e3eb;
      border-radius: 12px;
      padding: 25px 30px;
      display: flex;
      align-items: center;
      gap: 20px;
      transition: all 0.2s;
      cursor: default;
    }
    .dep-card:hover {
      box-shadow: 0 8px 24px rgba(59, 125, 155, 0.08);
      border-color: #bbd3e0;
      transform: translateY(-2px);
    }

    .dep-icon-box {
      width: 40px;
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }
    .dep-icon-box svg {
      width: 100%;
      height: 100%;
    }

    .dep-card-text {
      color: #1a3c53;
      font-weight: 500;
      font-size: 1rem;
      line-height: 1.3;
    }

    /* Responsive */
    @media (max-width: 900px) {
      .split-section { flex-direction: column; }
      .dep-grid { grid-template-columns: repeat(2, 1fr); }
      .hero-body { width: 80%; }
      .stats-banner { flex-wrap: wrap; gap: 20px; position: relative; border-radius: 0; background: #305D7A; }
      .hero-header { height: auto; display: flex; flex-direction: column; }
      .hero-content-wrapper { padding-bottom: 0; }
      .floating-patient-card { display: none; }
    }
    @media (max-width: 600px) {
      .dep-grid { grid-template-columns: 1fr; }
      .hero-title { font-size: 2.5rem; }
      .nav-menu { display: none; }
    }
  `]
})
export class HomeComponent { }
