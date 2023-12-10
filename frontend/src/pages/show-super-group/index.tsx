import { useShowSuperGroupLoaderData } from "./loader";
import { Link } from "react-router-dom";

export const ShowSuperGroupPage = () => {
  const superGroup = useShowSuperGroupLoaderData();

  return (
    <div className={"mx-auto mt-4 w-fit"}>
      <ul className={"list-disc"}>
        <li>{superGroup.name}</li>
        <li>{superGroup.prettyName}</li>
        <li>
          <Link to={"/types/" + superGroup.type}>{superGroup.type}</Link>
        </li>
        <li>{superGroup.svDescription}</li>
        <li>{superGroup.enDescription}</li>
      </ul>
    </div>
  );
};
