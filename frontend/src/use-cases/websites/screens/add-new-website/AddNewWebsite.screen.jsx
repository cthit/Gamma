import React from "react";
import EditWebsite from "../common-views/edit-website";
import { Center } from "../../../../common-ui/layout";

const AddNewWebsite = ({ websitesAdd }) => (
  <Center>
    <EditWebsite
      initialValues={{ name: "", prettyName: "" }}
      onSubmit={(values, actions) => {
        websitesAdd(values);
      }}
      titleText="Skapa ny website"
      submitText="Spara ny"
    />
  </Center>
);

export default AddNewWebsite;
