import { useMeLoaderData } from "./loader";

export const MePage = () => {
  const me = useMeLoaderData();

  return (
    <div className={"mx-auto mt-4 w-fit"}>
      <ul className={"list-disc"}>
        <img src={"/api/images/user/avatar/" + me.id} alt={"avatar"} />
        <li>{me.firstName + ' "' + me.nick + '" ' + me.lastName}</li>
        <li>Acceptance year: {me.acceptanceYear}</li>
        <li>Cid: {me.cid}</li>
      </ul>
      {me.groups.length > 0 && (
        <>
          <p className={"mt-4 font-bold"}>Groups:</p>
          <ul className={"list-disc mt-2"}>
            {me.groups.map(({ group, post }) => (
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
