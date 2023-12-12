import { useShowGroupLoaderData } from "./loader";
import { Link } from "react-router-dom";

export const ShowGroupPage = () => {
  const group = useShowGroupLoaderData();

  return (
    <div className={"mx-auto mt-4 w-fit"}>
      <img
        className={"w-20"}
        src={"/api/images/group/avatar/" + group.id}
        alt={"avatar"}
      />
      <img
        className={"w-40"}
        src={"/api/images/group/banner/" + group.id}
        alt={"banner"}
      />
      <ul className={"list-disc"}>
        <li>{group.name}</li>
        <li>{group.prettyName}</li>
        <li>
          <Link to={"/super-groups/" + group.superGroup.id}>
            {group.superGroup.prettyName}
          </Link>
        </li>
      </ul>
      Members:
      <ul>
        {group.groupMembers.map((member) => (
          <li key={member.user.id + member.post.id} className={"list-disc"}>
            <Link to={"/users/" + member.user.id}>
              {member.user.nick +
                " - " +
                member.post.svName +
                "/" +
                member.post.enName}
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
};
