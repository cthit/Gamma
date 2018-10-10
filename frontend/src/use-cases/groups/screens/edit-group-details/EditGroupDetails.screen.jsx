import React from "react";

import { Center } from "../../../../common-ui/layout";

import GroupForm from "../common-views/group-form";

const EditGroupDetails = ({ group, groupsChange, match }) => (
  <Center>
    <GroupForm
      onSubmit={(values, actions) => {
        groupsChange(values, match.params.id);
      }}
      initialValues={group}
    />
  </Center>
);

export default EditGroupDetails;
