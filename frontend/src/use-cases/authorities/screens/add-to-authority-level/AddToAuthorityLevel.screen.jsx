import React, { useEffect } from "react";
import { useParams } from "react-router-dom";
import { getAuthority } from "../../../../api/authorities/get.authorities";

const AddToAuthorityLevel = () => {
    const { id } = useParams();

    useEffect(() => {
        if (id == null) {
            return null;
        }

        getAuthority(id).then(response => {
            console.log(response);
        });
    }, [id]);

    return null;
};

export default AddToAuthorityLevel;
