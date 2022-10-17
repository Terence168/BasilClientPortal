const routes = [
  {
    path: "/",
    component: () => import("layouts/MainLayout.vue"),
    children: [
      {
        path: "privilege",
        name: "privilege",
        component: () => import("pages/PrivilegePage.vue"),
        meta: {
          requiresAuth: true,
          permissions: ["privilege"],
        },
        children: [
          {
            path: "role-type",
            name: "role-type",
            component: () => import("pages/RoleTypePage.vue"),
            meta: {
              requiresAuth: true,
              permissions: ["privilege.role-type"],
            },
          },

          {
            path: "role",
            name: "role",
            component: () => import("pages/RolePage.vue"),
            meta: {
              requiresAuth: true,
              permissions: ["privilege.role"],
            },
          },

          {
            path: "user",
            name: "user",
            component: () => import("pages/UserPage.vue"),
            meta: {
              requiresAuth: true,
              permissions: ["privilege.user"],
            },
          },
        ],
      },
      {
        path: "rma",
        name: "rma",
        component: () => import("pages/RMAPage.vue"),
        meta: {
          requiresAuth: true,
          permissions: ["rma"],
        },
        children: [
          {
            path: "status",
            name: "status",
            component: () => import("pages/StatusPage.vue"),
            meta: {
              requiresAuth: true,
              permissions: ["rma.status"],
            },
          },

          {
            path: "shipping",
            name: "shipping",
            component: () => import("pages/ShippingPage.vue"),
            meta: {
              requiresAuth: true,
              permissions: ["rma.shipping"],
            },
          },

          {
            path: "quarantine",
            name: "quarantine",
            component: () => import("pages/QuarantinePage.vue"),
            meta: {
              requiresAuth: true,
              permissions: ["rma.quarantine"],
            },
          },
        ],
      },
    ],
  },

  {
    path: "/login",
    name: "login",
    component: () => import("pages/LoginPage.vue"),
  },

  {
    path: "/activate-user/:token",
    name: "activate-user",
    component: () => import("pages/ActivateUserPage.vue"),
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: "/:catchAll(.*)*",
    component: () => import("pages/ErrorNotFound.vue"),
  },
];

export default routes;
