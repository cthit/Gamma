import React from "react";

import EditWebsite from "../common-views/edit-website";
import { Center } from "../../../../common-ui/layout";

const EditWebsiteDetails = ({ website }) => (
  <Center>
    <EditWebsite
      initialValues={website}
      onSubmit={(values, actions) => {
        console.log(values);
      }}
      titleText="Redigera hemsida"
      submitText="Spara hemsida"
    />
  </Center>
);

export default EditWebsiteDetails;
