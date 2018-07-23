import React from "react";
import styled from "styled-components";
import { Add } from "@material-ui/icons";

import { Heading, Title, Subtitle } from "../../common-ui/text";

import GammaTable from "../../common/views/gamma-table";

class Demo extends React.Component {
  state = {
    selected: []
  };

  render() {
    const { selected } = this.state;

    return (
      <div>
        <StyledGammaTable
          startOrderBy="firstName"
          columnsOrder={[
            "firstName",
            "nick",
            "lastName",
            "id",
            "acceptanceYear"
          ]}
          headerTexts={{
            firstName: "First name",
            nick: "Nick",
            lastName: "Last name",
            id: "Cid",
            acceptanceYear: "Acceptance Year",
            __link: "Detaljer",
            __checkbox: "Välj användare"
          }}
          data={[
            {
              id: "svensven",
              nick: "Portals",
              firstName: "Sven",
              lastName: "Svensson",
              acceptanceYear: 2018,
              __link: "/login"
            },
            {
              id: "andand",
              nick: "Gurr",
              firstName: "Anders",
              lastName: "Andersson",
              acceptanceYear: 2015,
              __link: "/login"
            },
            {
              id: "glasss",
              nick: "NeonSky",
              firstName: "Glass",
              lastName: "Glasson",
              acceptanceYear: 2001,
              __link: "/login"
            },
            {
              id: "icesson",
              nick: "Iller",
              firstName: "Ice",
              lastName: "Icesson",
              acceptanceYear: 2003,
              __link: "/login"
            },
            {
              id: "pajpaj",
              nick: "Pi",
              firstName: "Paj",
              lastName: "Pajsson",
              acceptanceYear: 2005,
              __link: "/login"
            },
            {
              id: "asdfa",
              nick: "LP",
              firstName: "Asdf",
              lastName: "Asdfsson",
              acceptanceYear: 2007
            },
            {
              id: "glads",
              nick: "Vidde",
              firstName: "Glad",
              lastName: "Gladsson",
              acceptanceYear: 2009,
              __link: "/login"
            },
            {
              id: "hmhmm",
              nick: "Jerge",
              firstName: "Hmm",
              lastName: "Hmmsson",
              acceptanceYear: 2011,
              __link: "/login"
            }
          ]}
          selected={selected}
          onSelectedUpdated={newSelected =>
            this.setState({
              selected: newSelected
            })
          }
        />
      </div>
    );
  }
}

const StyledGammaTable = styled(GammaTable)``;

export default Demo;
