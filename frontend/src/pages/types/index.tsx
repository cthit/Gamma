import { useTypesLoaderData } from "./loader";
import * as z from "zod";
import { useRevalidator } from "react-router-dom";
import { GammaClient } from "../../client/gamma";

const createTypesValidation = z
  .object({
    type: z.string(),
  })
  .strict();

export const TypesPage = () => {
  const types = useTypesLoaderData();
  const revalidator = useRevalidator();

  const onSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const form = Object.fromEntries(new FormData(e.currentTarget));

    const validatedForm = createTypesValidation.parse(form);
    GammaClient.instance()
      .types.addType(validatedForm.type)
      .then(() => revalidator.revalidate());
  };

  const deleteType = (type: string) => () => {
    GammaClient.instance()
      .types.deleteType(type)
      .then(() => {
        revalidator.revalidate();
      });
  };

  return (
    <div className={"mx-auto mt-4 w-fit flex flex-col gap-4"}>
      <ul className={"list-disc"}>
        {types.map((type) => (
          <li key={type} className={"flex flex-row gap-2"}>
            <p>{type}</p>
            <button onClick={deleteType(type)}>Delete</button>
          </li>
        ))}
      </ul>

      <div className={"outline flex flex-col gap-2"}>
        <form className={"contents"} onSubmit={onSubmit}>
          <label htmlFor={"newType"}>Add new type</label>
          <input
            className={"outline"}
            type="text"
            id={"newType"}
            name="type"
            placeholder={"Type Name"}
          />
          <button type={"submit"}>Add type</button>
        </form>
      </div>
    </div>
  );
};
