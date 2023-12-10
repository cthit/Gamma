import { createBrowserRouter } from "react-router-dom";
import { AboutPage } from "./pages/about";
import { Root } from "./root";
import { HomePage } from "./pages/home";
import { homeLoader } from "./pages/home/loader";
import { ErrorPage } from "./pages/error";
import { UsersPage } from "./pages/users";
import { usersLoader } from "./pages/users/loader";
import { ShowUserPage } from "./pages/show-user";
import { showUserLoader } from "./pages/show-user/loader";
import { GroupsPage } from "./pages/groups";
import { groupsLoader } from "./pages/groups/loader";
import { ShowGroupPage } from "./pages/show-group";
import { showGroupLoader } from "./pages/show-group/loader";
import { SuperGroupsPage } from "./pages/super-groups";
import { superGroupsLoader } from "./pages/super-groups/loader";
import { ShowSuperGroupPage } from "./pages/show-super-group";
import { showSuperGroupLoader } from "./pages/show-super-group/loader";
import { TypesPage } from "./pages/types";
import { typesLoader } from "./pages/types/loader";
import { ClientsPage } from "./pages/clients";
import { clientsLoader } from "./pages/clients/loader";
import { ShowClientPage } from "./pages/show-client";
import { showClientLoader } from "./pages/show-client/loader";
import { PostsPage } from "./pages/posts";
import { postsLoader } from "./pages/posts/loader";
import { ShowPostPage } from "./pages/show-post";
import { showPostLoader } from "./pages/show-post/loader";
import { AllowListPage } from "./pages/allow-list";
import { allowListLoader } from "./pages/allow-list/loader";
import { ActivationCodesPage } from "./pages/activation-codes";
import { activationCodesLoader } from "./pages/activation-codes/loader";
import { MePage } from "./pages/me";
import { meLoader } from "./pages/me/loader";
import { EnterCidPage } from "./pages/create-account/enter-cid";
import { EmailSentPage } from "./pages/create-account/email-sent";
import { InputNewAccountPage } from "./pages/create-account/input-new-account";
import { AdminsPage } from "./pages/admins";
import { adminsLoader } from "./pages/admins/loader";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <Root />,
    errorElement: <ErrorPage />,
    children: [
      { path: "", element: <HomePage />, loader: homeLoader },
      { path: "/me", element: <MePage />, loader: meLoader },
      { path: "/about", element: <AboutPage /> },
      {
        path: "/create-account",
        element: <EnterCidPage />,
      },
      { path: "/create-account/email-sent", element: <EmailSentPage /> },
      { path: "/create-account/input", element: <InputNewAccountPage /> },
      {
        path: "/users/:id",
        element: <ShowUserPage />,
        loader: ({ params }) => {
          if (params.id === undefined) {
            throw new Error("Unexpected undefined id in params");
          }

          return showUserLoader(params.id);
        },
      },
      { path: "/users", element: <UsersPage />, loader: usersLoader },
      {
        path: "/groups/:id",
        element: <ShowGroupPage />,
        loader: ({ params }) => {
          if (params.id === undefined) {
            throw new Error("Unexpected undefined id in params");
          }

          return showGroupLoader(params.id);
        },
      },
      { path: "/groups", element: <GroupsPage />, loader: groupsLoader },
      {
        path: "/super-groups/:id",
        element: <ShowSuperGroupPage />,
        loader: ({ params }) => {
          if (params.id === undefined) {
            throw new Error("Unexpected undefined id in params");
          }

          return showSuperGroupLoader(params.id);
        },
      },
      {
        path: "/super-groups",
        element: <SuperGroupsPage />,
        loader: superGroupsLoader,
      },
      {
        path: "/types",
        element: <TypesPage />,
        loader: typesLoader,
      },
      {
        path: "/clients/:id",
        element: <ShowClientPage />,
        loader: ({ params }) => {
          if (params.id === undefined) {
            throw new Error("Unexpected undefined id in params");
          }

          return showClientLoader(params.id);
        },
      },
      {
        path: "/clients",
        element: <ClientsPage />,
        loader: clientsLoader,
      },
      {
        path: "/posts/:id",
        element: <ShowPostPage />,
        loader: ({ params }) => {
          if (params.id === undefined) {
            throw new Error("Unexpected undefined id in params");
          }

          return showPostLoader(params.id);
        },
      },
      {
        path: "/posts",
        element: <PostsPage />,
        loader: postsLoader,
      },
      {
        path: "/allow-list",
        element: <AllowListPage />,
        loader: allowListLoader,
      },
      {
        path: "/activation-codes",
        element: <ActivationCodesPage />,
        loader: activationCodesLoader,
      },
      {
        path: "/admins",
        element: <AdminsPage />,
        loader: adminsLoader,
      },
    ],
  },
]);
