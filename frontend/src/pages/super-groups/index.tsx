import { useSuperGroupsLoaderData } from "./loader";
import { Link, useRevalidator } from "react-router-dom";
import { GammaClient } from "../../client/gamma";
import * as z from "zod";

const tableCellStyle = "border border-slate-300 p-1";

const createSuperGroupValidation = z
  .object({
    name: z.string(),
    prettyName: z.string(),
    type: z.string(),
    svDescription: z.string(),
    enDescription: z.string(),
  })
  .strict();

export const SuperGroupsPage = () => {
  const { superGroups, types } = useSuperGroupsLoaderData();

  const revalidator = useRevalidator();

  const onSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const form = Object.fromEntries(new FormData(e.currentTarget));

    const validatedForm = createSuperGroupValidation.parse(form);
    GammaClient.instance()
      .superGroups.createSuperGroup(validatedForm)
      .then(() => revalidator.revalidate());
  };

  const deleteSuperGroup = (id: string) => () => {
    GammaClient.instance()
      .superGroups.deleteSuperGroup(id)
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
            <th className={tableCellStyle + " text-left"}>Type</th>
            <th className={tableCellStyle + " text-left"}>Details</th>
            <th className={tableCellStyle + " text-left"}>Delete</th>
          </tr>
        </thead>
        <tbody>
          {superGroups.map((superGroup) => (
            <tr key={superGroup.id}>
              <td className={tableCellStyle}>{superGroup.prettyName}</td>
              <td className={tableCellStyle}>
                <Link to={"/types/" + superGroup.type}>{superGroup.type}</Link>
              </td>
              <td className={tableCellStyle}>
                <Link to={"/super-groups/" + superGroup.id}>Details</Link>
              </td>
              <td className={tableCellStyle}>
                <button onClick={deleteSuperGroup(superGroup.id)}>
                  Delete
                </button>
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
          <input
            className={"outline"}
            type="text"
            name="svDescription"
            placeholder={"Swedish description"}
          />
          <input
            className={"outline"}
            type="text"
            name="enDescription"
            placeholder={"English description"}
          />
          <select name={"type"}>
            {types.map((type) => (
              <option key={type} value={type}>
                {type}
              </option>
            ))}
          </select>
          <button type={"submit"}>Add group</button>
        </form>
      </div>
    </div>
  );
};
