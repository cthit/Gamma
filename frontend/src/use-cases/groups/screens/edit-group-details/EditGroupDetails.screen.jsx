import React from "react";

import { Center } from "../../../../common-ui/layout";

import EditGroupInformation from "../common-views/edit-group-information";

const EditGroupDetails = ({ group, groupsChange, match }) => (
  <Center>
    <EditGroupInformation
      onSubmit={(values, actions) => {
        groupsChange(values, match.params.id);
      }}
      initialValues={group}
    />
  </Center>
);

export default EditGroupDetails;
