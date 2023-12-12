import { useShowUserLoaderData } from "./loader";

export const ShowUserPage = () => {
  const { user, groups } = useShowUserLoaderData();

  return (
    <div className={"mx-auto mt-4 w-fit"}>
      <img src={"/api/images/user/avatar/" + user.id} alt={"avatar"} />
      <ul className={"list-disc"}>
        <li>{user.firstName + ' "' + user.nick + '" ' + user.lastName}</li>
        <li>Acceptance year: {user.acceptanceYear}</li>
        <li>Cid: {user.cid}</li>
      </ul>
      {groups.length > 0 && (
        <>
          <p className={"mt-4 font-bold"}>Groups:</p>
          <ul className={"list-disc mt-2"}>
            {groups.map(({ group, post }) => (
              <li key={group.id + post.id}>
                {group.prettyName} - {post.enName}/{post.svName}
              </li>
            ))}
          </ul>
        </>
      )}
    </div>
  );
};
