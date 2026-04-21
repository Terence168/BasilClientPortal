import { route } from "quasar/wrappers";
import {
  createRouter,
  createMemoryHistory,
  createWebHistory,
  createWebHashHistory,
} from "vue-router";
import routes from "./routes";

/*
 * If not building with SSR mode, you can
 * directly export the Router instantiation;
 *
 * The function below can be async too; either use
 * async/await or return a Promise which resolves
 * with the Router instance.
 */

export default route(function (/* { store, ssrContext } */) {
  const createHistory = process.env.SERVER
    ? createMemoryHistory
    : process.env.VUE_ROUTER_MODE === "history"
    ? createWebHistory
    : createWebHashHistory;

  const Router = createRouter({
    // scrollBehavior: () => ({ left: 0, top: 0 }),
    routes,

    // Leave this as is and make changes in quasar.conf.js instead!
    // quasar.conf.js -> build -> vueRouterMode
    // quasar.conf.js -> build -> publicPath
    history: createHistory(process.env.VUE_ROUTER_BASE),
  });

  // 新部署后若入口 HTML 被缓存，懒加载 chunk 的 hash 与服务器不一致会导致动态 import 失败；整页刷新一次以拉取最新入口（同标签页仅尝试一次，避免死循环）
  const chunkReloadKey = "basil_portal_chunk_reload_attempted";
  Router.onError((error) => {
    const msg = error?.message || String(error);
    const isChunkLoadFailure =
      /Failed to fetch dynamically imported module|Loading chunk \d+ failed/i.test(
        msg
      );
    if (!isChunkLoadFailure) return;
    if (sessionStorage.getItem(chunkReloadKey)) return;
    sessionStorage.setItem(chunkReloadKey, "1");
    window.location.reload();
  });

  return Router;
});
