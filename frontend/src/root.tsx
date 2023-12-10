import { Link, Outlet } from "react-router-dom";

export const Root = () => {
  return (
    <div className="w-screen h-screen">
      <header className="relative w-full h-40 shadow-lg">
        <img
          className={"object-cover w-full h-full"}
          src={"enbarsskar.jpg"}
          alt={"Enbärsskär"}
        />
        <h1
          className={"absolute bottom-4 left-2 text-white text-2xl font-bold"}
        >
          <Link to={"/"}>Gamma - IT account</Link>
        </h1>
      </header>
      <div className={"p-4"}>
        <Outlet />
      </div>
    </div>
  );
};
