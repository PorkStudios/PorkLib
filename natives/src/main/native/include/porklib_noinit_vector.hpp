#pragma once

#include <memory>
#include <vector>

namespace porklib {
    template<typename T>
    using noinit_vector = std::vector<T>; //TODO: use boost::noinit_adaptor or something
}
