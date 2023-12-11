import { useGroupsLoaderData } from "./loader";
import { Link, useRevalidator } from "react-router-dom";
import { GammaClient } from "../../client/gamma";
import * as z from "zod";

const tableCellStyle = "border border-slate-300 p-1";

const createGroupValidation = z.object({
  name: z.string(),
  prettyName: z.string(),
  superGroup: z.string(),
});

export const GroupsPage = () => {
  const { groups, superGroups } = useGroupsLoaderData();
  const revalidator = useRevalidator();

  const onSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const form = Object.fromEntries(new FormData(e.currentTarget));

    const validatedForm = createGroupValidation.parse(form);
    GammaClient.instance()
      .groups.createGroup(validatedForm)
      .then(() => revalidator.revalidate());
  };

  const deleteGroup = (type: string) => () => {
    GammaClient.instance()
      .types.deleteType(type)
      .then(() => {
        revalidator.revalidate();
      });
  };

  return (
    <div className={"mx-auto mt-4 w-fit flex flex-col gap-4 w-1/2"}>
      <table
        className={
          "m-auto w-full table-fixed border-collapse border border-slate-400"
        }
      >
        <thead>
          <tr>
            <th className={tableCellStyle + " text-left"}>Name</th>
            <th className={tableCellStyle + " text-left"}>Super group</th>
            <th className={tableCellStyle + " text-left"}>Details</th>
            <th className={tableCellStyle + " text-left"}>Delete</th>
          </tr>
        </thead>
        <tbody>
          {groups.map((group) => (
            <tr key={group.id}>
              <td className={tableCellStyle}>{group.prettyName}</td>
              <td className={tableCellStyle}>
                <Link to={"/super-groups/" + group.superGroup.id}>
                  {group.superGroup.prettyName}
                </Link>
              </td>
              <td className={tableCellStyle}>
                <Link to={"/groups/" + group.id}>Details</Link>
              </td>
              <td>
                <button onClick={deleteGroup(group.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className={"outline flex flex-col gap-2"}>
        <form className={"contents"} onSubmit={onSubmit}>
          <input
            className={"outline"}
            type="text"
            name="name"
            placeholder={"Name"}
          />
          <input
            className={"outline"}
            type="text"
            name="prettyName"
            placeholder={"Pretty Name"}
          />
          <select name={"superGroup"}>
            {superGroups.map((superGroup) => (
              <option key={superGroup.id} value={superGroup.id}>
                {superGroup.prettyName}
              </option>
            ))}
          </select>
          <button type={"submit"}>Add group</button>
        </form>
      </div>
    </div>
  );
};
