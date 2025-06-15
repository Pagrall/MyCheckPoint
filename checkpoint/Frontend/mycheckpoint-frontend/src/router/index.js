import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView/HomeView.vue";
import LoginView from "../views/LoginView/LoginView.vue";
import RegisterView from "../views/RegisterView/RegisterView.vue";
import UserSettingsView from "../views/UserSettingsView/UserSettingsView.vue";
import UserProfileView from "../views/UserProfileView/UserProfileView.vue";
// MyLibraryView, MyGameListsView, MyTierListsView serán ahora parte de UserProfileView o cargados por él
import FriendsPageView from "../views/FriendsPageView/FriendsPageView.vue";
import RequestPasswordResetView from "../views/RequestPasswordResetView/RequestPasswordResetView.vue";
import ResetPasswordView from "../views/ResetPasswordView/ResetPasswordView.vue";
import SearchGamesView from "../views/SearchGamesView/SearchGamesView.vue";
import GameDetailsView from "../views/GameDetails/GameDetailsView.vue";

// Vistas para las listas/tiers públicos y búsqueda de usuarios
import ViewPublicGameListsView from "../views/ViewPublicGameListsView/ViewPublicGameListsView.vue"; // Crear este placeholder
import ViewPublicTierListsView from "../views/ViewPublicTierListsView/ViewPublicTierListsView.vue"; // Crear este placeholder
import TierListDetail from "@/views/TierListDetailView/TierListDetail.vue";
import GameListDetailView from "../views/GameListDetailView/GameListDetailView.vue"; // Crear este placeholder
import SearchUsersView from "../views/SearchUsersView/SearchUsersView.vue"; // Crear este placeholder

import { useAuthStore } from "@/stores/authStore";

const routes = [
  { path: "/", name: "home", component: HomeView },
  {
    path: "/login",
    name: "login",
    component: LoginView,
    meta: { guestOnly: true },
  },
  {
    path: "/register",
    name: "register",
    component: RegisterView,
    meta: { guestOnly: true },
  },
  {
    path: "/request-password-reset",
    name: "request-password-reset",
    component: RequestPasswordResetView,
    meta: { guestOnly: true },
  },
  {
    path: "/resetear-password",
    name: "reset-password",
    component: ResetPasswordView,
    meta: { guestOnly: true },
  },
  {
    path: "/profile/:publicId",
    name: "profile",
    component: UserProfileView,
    props: true,
  },
  {
    path: "/settings", 
    name: "settings",
    component: UserSettingsView, 
    meta: { requiresAuth: true },
  },
  {
    path: "/friends",
    name: "friends",
    component: FriendsPageView,
    meta: { requiresAuth: true },
  },
  {
    path: "/search-games",
    name: "search-games",
    component: SearchGamesView,
  },
  {
    path: "/games/:igdbId/details",
    name: "game-details",
    component: GameDetailsView,
    props: true,
  },
  {
    path: "/public-gamelists",
    name: "public-gamelists",
    component: ViewPublicGameListsView, 
  },
  {
    path: "/gamelists/public/:listPublicId", 
    name: "view-public-gamelist",
    component: GameListDetailView, 
    props: true,
  },
  {
    path: "/my-gamelists/:listPublicId/detail", 
    name: "gamelist-detail",
    component: GameListDetailView,
    props: true, 
    meta: { requiresAuth: true }, 
  },
  {
    path: "/public-tierlists",
    name: "public-tierlists",
    component: ViewPublicTierListsView, 
  },
  {
    path: "/tierlists/public/:tierListPublicId", 
    name: "view-public-tierlist",
    component: TierListDetail, 
    props: true,
  },
  {
    path: "/search-users",
    name: "search-users",
    component: SearchUsersView, 
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();

  if (to.matched.some((record) => record.meta.requiresAuth)) {
    if (!authStore.isAuthenticated) {
      next({ name: "login", query: { redirect: to.fullPath } });
    } else {
      next();
    }
  } else if (to.matched.some((record) => record.meta.guestOnly)) {
    if (authStore.isAuthenticated) {
      next({ name: "home" });
    } else {
      next();
    }
  } else {
    next();
  }
});

export default router;
