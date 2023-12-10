import { FC } from "react";
import { useHomeLoaderData } from "./loader";
import { Link } from "react-router-dom";

export const HomePage: FC = () => {
  const data = useHomeLoaderData();

  return (
    <div className={"mx-auto mt-4 w-fit"}>
      <h2 className={"text-xl"}>Welcome, {data.user.nick}</h2>
      <ul className={"list-disc"}>
        <li>
          <Link to={"/me"}>Me</Link>
        </li>
        <li>
          <Link to={"/users"}>Users</Link>
        </li>
        <li>
          <Link to={"/groups"}>Groups</Link>
        </li>
        <li>
          <Link to={"/super-groups"}>Super groups</Link>
        </li>
        <li>
          <Link to={"/Posts"}>Posts</Link>
        </li>
        {data.user.isAdmin && (
          <>
            <li>
              <Link to={"/types"}>Types</Link>
            </li>
            <li>
              <Link to={"/clients"}>Clients</Link>
            </li>
            <li>
              <Link to={"/allow-list"}>Allow list</Link>
            </li>
            <li>
              <Link to={"/activation-codes"}>Activation codes</Link>
            </li>
            <li>
              <Link to={"/admins"}>Admins</Link>
            </li>
          </>
        )}
      </ul>
    </div>
  );
};
