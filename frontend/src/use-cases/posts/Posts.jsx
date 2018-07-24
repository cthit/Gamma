import React from "react";
import GammaTable from "../../common/views/gamma-table";

class Posts extends React.Component {
  constructor(props) {
    super();

    props.postsLoad();
  }

  render() {
    return <div>hej</div>;
  }
}

export default Posts;
